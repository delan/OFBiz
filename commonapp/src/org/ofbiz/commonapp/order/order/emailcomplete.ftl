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

<#assign localOrderReadHelper = Static["org.ofbiz.commonapp.order.order.OrderReadHelper"].getHelper(orderHeader)>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <#-- this needs to be fully qualified to appear in email; the server must also be available -->
    <link rel='stylesheet' href='${baseUrl}/images/maincss.css' type='text/css'>      
</head>

<body> 

<#-- custom logo or text can be inserted here -->

<p class="head1">Final Order Notification</p>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>

<#-- order header -->
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <#-- left side -->
    <td width='50%' valign='top' align='left'>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <#-- general order info -->
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;Order&nbsp;<#if orderHeader?has_content>#${orderHeader.orderId}&nbsp;</#if>Information</div>
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
                    <#-- placing customer information -->
                    <#if placingCustomerPerson?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Name</b></div>
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
                        <div class="tabletext">&nbsp;<b>Status</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <#if orderHeader?has_content>                                                
                          <div class="tabletext">${statusString?default("N/A")}</div>
                        <#else>
                          <div class="tabletext"><b>Not Yet Ordered</b></div>
                        </#if>
                      </td>
                    </tr>
                    <#-- ordered date -->
                    <#if orderHeader?has_content>   
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Date</b></div>
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
                          <div class="tabletext">&nbsp;<b>Distributor</b></div>
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
                              <div class="tabletext" align="center">${paymentAddress.city}<#if paymentAddress.stateProvinceGeoId?has_content>, ${paymentAddress.stateProvinceGeoId}</#if> ${paymentAddress.postalCode?if_exists}
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
                            <div class="tabletext">&nbsp;<b>EFT Account</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              ${eftAccount.nameOnAcction}<br>
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
    </td>
    <td bgcolor="white" width="1">&nbsp;&nbsp;</td>
    <#-- right side -->
    <td width='50%' valign='top' align='left'>
      <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>        
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;Shipping Information</div>
                </td>
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
                            ${shippingAddress.postalCode?if_exists}<br>
                            ${shippingAddress.countryGeoId?if_exists}
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
                          <#if !maySplit?default(false)>Please wait until the entire order is ready before shipping.</#if>
                          <#if maySplit?default(false)>Please ship items I ordered as they become available (may incur additional shipping charges).</#if>
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
                          <#if !isGift?default(false)>This order is not a gift.</#if>
                          <#if isGift?default(false)>This order is a gift.</#if>
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
    </td>
  </tr>
</table>

<br>

<#-- order items -->
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Order Items</div>
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
            <table width="100%" border="0" cellpadding="0">
              <tr align='left' valign='bottom'>
                <td width="65%" align="left"><span class="tableheadtext"><b>Product</b></span></td>               
                <td width="5%" align="right"><span class="tableheadtext"><b>Quantity</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>Unit Price</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>Adjustments</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>Subtotal</b></span></td>
              </tr>
              <#list orderItems as orderItem>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>     
                  <#if orderItem.productId?if_exists == "_?_">           
                    <td colspan="1" valign="top">    
                      <b><div class="tabletext"> &gt;&gt; ${orderItem.itemDescription}</div></b>
                    </td>
                  <#else>                  
                    <td valign="top">
                      <div class="tableheadtext">${orderItem.productId?default("N/A")} - ${orderItem.itemDescription}</div>
                    </td>                   
                    <td align="right" valign="top">
                      <div class="tabletext" nowrap>${orderItem.quantity?string.number}</div>
                    </td>
                    <td align="right" valign="top">
                      <div class="tabletext" nowrap>${orderItem.unitPrice?string.currency}</div>
                    </td>
                    <td align="right" valign="top">
                      <div class="tabletext" nowrap>${localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem)?string.currency}</div>
                    </td>
                    <td align="right" valign="top" nowrap>
                      <div class="tabletext">${localOrderReadHelper.getOrderItemTotal(orderItem)?string.currency}</div>
                    </td>
                    <#if maySelectItems?default(false)>
                      <td>                                 
                        <input name="item_id" value="${orderItem.orderItemSeqId}" type="checkbox">
                      </td>
                    </#if>
                  </#if>
                </tr>
                
                <#-- now show adjustment details per line item -->
                <#assign itemAdjustments = localOrderReadHelper.getOrderItemAdjustments(orderItem)>
                <#list itemAdjustments as orderItemAdjustment>
                  <tr>
                    <td align="right">
                      <div class="tabletext" style='font-size: xx-small;'>
                        <b><i>Adjustment</i>:</b> <b>${localOrderReadHelper.getAdjustmentType(orderItemAdjustment)}</b>&nbsp;
                        <#if orderItemAdjustment.description?has_content>: ${orderItemAdjustment.description}</#if>
                      </div>
                    </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td align="right">
                      <div class="tabletext" style='font-size: xx-small;'>${localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment)?string.currency}</div>
                    </td>
                    <td>&nbsp;</td>
                    <#if maySelectItems?default(false)><td>&nbsp;</td></#if>
                  </tr>
                </#list>
               </#list>
               <#if orderItems?size == 0 || !orderItems?has_content>
                 <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
               </#if>

              <tr><td colspan="8"><hr class='sepbar'></td></tr>
              <tr>
                <td align="right" colspan="4"><div class="tabletext"><b>Subtotal</b></div></td>
                <td align="right" nowrap><div class="tabletext">${orderSubTotal?string.currency}</div></td>
              </tr>              
              <#list headerAdjustmentsToShow as orderHeaderAdjustment>                
                <tr>
                  <td align="right" colspan="4"><div class="tabletext"><b>${localOrderReadHelper.getAdjustmentType(orderHeaderAdjustment)}</b></div></td>
                  <td align="right" nowrap><div class="tabletext">${localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)?string.currency}</div></td>
                </tr>
              </#list>
              <tr>
                <td align="right" colspan="4"><div class="tabletext"><b>Shipping and Handling</b></div></td>
                <td align="right" nowrap><div class="tabletext">${orderShippingTotal?string.currency}</div></td>
              </tr>              
              <tr>
                <td align="right" colspan="4"><div class="tabletext"><b>Sales Tax</b></div></td>
                <td align="right" nowrap><div class="tabletext">${orderTaxTotal?string.currency}</div></td>
              </tr>
              
              <tr><td colspan=2></td><td colspan="8"><hr class='sepbar'></td></tr>
              <tr>
                <td align="right" colspan="4"><div class="tabletext"><b>Grand Total</b></div></td>
                <td align="right" nowrap>
                  <div class="tabletext">${orderGrandTotal?string.currency}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

</body>  
</html> 