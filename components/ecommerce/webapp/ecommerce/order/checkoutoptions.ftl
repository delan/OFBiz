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
 *@version    $Revision: 1.1 $
 *@since      2.1
-->

<script language="javascript" type="text/javascript">
<!--
function submitForm(form, mode, value) {	
	if (mode == "DN") {	
		// done action; checkout
		form.action="<@ofbizUrl>/checkout</@ofbizUrl>";
		form.submit();
	} else if (mode == "CS") {	
		// continue shopping	
		form.action="<@ofbizUrl>/updateCheckoutOptions/showcart</@ofbizUrl>";
		form.submit();
	} else if (mode == "NA") {
		// new address
		form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</@ofbizUrl>";
		form.submit();
	} else if (mode == "EA") {
		// edit address
		form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutoptions&contactMechId="+value+"</@ofbizUrl>";
		form.submit();
	} else if (mode == "NC") {
		// new credit card
		form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions</@ofbizUrl>";
		form.submit();
	} else if (mode == "EC") {
		// edit credit card
		form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
		form.submit();
	} else if (mode == "NE") {
		// new eft account
		form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions</@ofbizUrl>";
		form.submit();
	} else if (mode == "EE") {
		// edit eft account
		form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
		form.submit();
	} else if (mode == "SA") {
        // selected shipping address
        form.action="<@ofbizUrl>/updateCheckoutOptions/checkoutoptions</@ofbizUrl>";
        form.submit();
    }
}
// -->
</script>

<#assign cart = context.shoppingCart?if_exists>

<form method="post" name="checkoutInfoForm" style='margin:0;'>
  <table width="100%" border="0" cellpadding='0' cellspacing='0'>
    <tr valign="top" align="left">
      <td height='100%'>
        <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign=middle align=left>
                    <div class="boxhead">1)&nbsp;How&nbsp;shall&nbsp;we&nbsp;ship&nbsp;it?</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr style='height: 100%;'>
            <td width='100%' valign=top height='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
                <tr>
                  <td>
                    <table width='100%' cellpadding='1' border='0' cellpadding='0' cellspacing='0'>
                      <#list context.carrierShipmentMethodList as carrierShipmentMethod>
                        <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
                        <tr>
                          <td width='1%' valign="top" >                            
                            <input type='radio' name='shipping_method' value='${shippingMethod}' <#if shippingMethod == context.chosenShippingMethod?default("N@A")>checked</#if>>       
                          </td>
                          <td valign="top">                            
                            <div class='tabletext'>
                              <#if cart.getShippingContactMechId()?exists>
                                <#assign shippingEstMap = Static["org.ofbiz.commonapp.shipment.shipment.ShippingEvents"].getShipEstimate(delegator, cart, shippingMethod)>
                                <#if shippingEstMap?has_content && shippingEstMap.shippingTotal?exists>
                                  <#assign shippingEstimate = " - " + shippingEstMap.shippingTotal?string.currency>
                                <#else>
                                  <#assign shippingEstimate = " - Calculated Offline">
                                </#if>                              
                              </#if>
                              <#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}${shippingEstimate?if_exists}
                            </div>                           
                          </td>
                        </tr>                        
                      </#list>
                      <#if !carrierShipmentMethodList?exists || carrierShipmentMethodList?size == 0>                     
                        <tr>
                          <td width='1%' valign="top">
                            <input type='radio' name='shipping_method' value="Default" checked>
                          </td>
                          <td valign="top">
                            <div class='tabletext'>Use Default: No other shipping methods available.</div>
                          </td>
                        </tr>
                      </#if>
                      <tr><td colspan='2'><hr class='sepbar'></td></tr>
                      <tr>
                        <td colspan='2'>
                          <div class="head2"><b>Ship all at once, or 'as available'?</b></div>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top">
                          <input type='radio' <#if !cart.getMaySplit()?default(false)>checked</#if> name='may_split' value='false'>
                        </td>
                        <td valign="top">
                          <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top">
                          <input <#if cart.getMaySplit()?default(false)>checked</#if> type='radio' name='may_split' value='true'>
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
                          <textarea class='textAreaBox' cols="30" rows="3" name="shipping_instructions">${cart.getShippingInstructions()?if_exists}</textarea>
                        </td>
                      </tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>       
                      <tr>
                        <td colspan="2">
                          <span class="head2"><b>PO Number</b></span>&nbsp;
                          <#if cart.getPoNumber()?exists && cart.getPoNumber() != "(none)">
                            <#assign currentPoNumber = cart.getPoNumber()>
                          </#if>
                          <input type="text" class='inputBox' name="corresponding_po_id" size="15" value='${currentPoNumber?if_exists}'>
                        </td>
                      </tr>                                                           
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>                      
                      <tr>
                        <td colspan="2">
                          <div>
                            <span class="head2"><b>Is This a Gift?</b></span>
                            <input type='radio' <#if cart.getIsGift()?default(false)>checked</#if> name='is_gift' value='true'><span class='tabletext'>Yes</span>
                            <input type='radio' <#if !cart.getIsGift()?default(false)>checked</#if> name='is_gift' value='false'><span class='tabletext'>No</span>
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
                          <textarea class='textAreaBox' cols="30" rows="3" name="gift_message">${cart.getGiftMessage()?if_exists}</textarea>
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
                              <#list context.emailList as email>
                                ${email.infoString?if_exists}<#if email_has_next>,</#if>
                              </#list>
                            </b>
                          </div>
                          <div class="tabletext">Your may update these in your <a href="<@ofbizUrl>/viewprofile?DONE_PAGE=checkoutoptions</@ofbizUrl>" class="buttontext">profile</a>.</div>
                          <br>
                          <div class="tabletext">You may add other comma separated email addresses here that will be used only for the current order:</div>
                          <input type="text" class='inputBox' size="30" name="order_additional_emails" value='${cart.getOrderAdditionalEmails()?if_exists}'>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
      <td bgcolor="white" width="1">&nbsp;&nbsp;</td>   
      <td height='100%'>
        <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside' style='height: 100%;'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign=middle align=left>
                    <div class="boxhead">2)&nbsp;Where&nbsp;shall&nbsp;we&nbsp;ship&nbsp;it?</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr style='height: 100%;'>
            <td width='100%' valign=top height='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
                <tr>
                  <td valign='top'>
                    <table width="100%" border="0" cellpadding="1" cellspacing="0">
                      <tr>
                        <td colspan="2">
                          <a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');" class="buttontext">[Add New Address]</a>
                        </td>
                      </tr>
                       <#if context.shippingContactMechList?has_content>
                         <tr><td colspan="2"><hr class='sepbar'></td></tr>
                         <#list context.shippingContactMechList as shippingContactMech>
                           <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
                           <tr>
                             <td align="left" valign="top" width="1%" nowrap>
                               <input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}"  onclick="javascript:submitForm(document.checkoutInfoForm, 'SA', null);"<#if cart.getShippingContactMechId()?default("") == shippingAddress.contactMechId> checked</#if>>        
                             </td>
                             <td align="left" valign="top" width="99%" nowrap>
                               <div class="tabletext">
                                 <#if shippingAddress.toName?has_content><b>To:</b>&nbsp;${shippingAddress.toName}<br></#if>
                                 <#if shippingAddress.attnName?has_content><b>Attn:</b>&nbsp;${shippingAddress.attnName}<br></#if>
                                 <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br></#if>
                                 <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br></#if>
                                 <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                                 <#if shippingAddress.stateProvinceGeoId?has_content><br>${shippingAddress.stateProvinceGeoId}</#if>
                                 <#if shippingAddress.postalCode?has_content><br>${shippingAddress.postalCode}</#if>
                                 <#if shippingAddress.countryGeoId?has_content><br>${shippingAddress.countryGeoId}</#if>                                                            
                                 <a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');" class="buttontext">[Update]</a>
                               </div>
                             </td>
                           </tr>
                           <tr><td colspan="2"><hr class='sepbar'></td></tr>
                         </#list>
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
      <td height='100%'>
        <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside' style='height: 100%;'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign=middle align=left>
                    <div class="boxhead">3)&nbsp;How&nbsp;shall&nbsp;you&nbsp;pay?</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr style='height: 100%;'>
            <td width='100%' valign=top height='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
                <tr>
                  <td valign=top>                
                    <table width="100%" cellpadding="1" cellspacing="0" border="0">
                      <tr><td colspan="2">
                        <span class='tabletext'>Add:</span>
                        <a href="javascript:submitForm(document.checkoutInfoForm, 'NC', '');" class="buttontext">[Credit Card]</a>
                        <a href="javascript:submitForm(document.checkoutInfoForm, 'NE', '');" class="buttontext">[EFT Account]</a>
                      </td></tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Mail&nbsp;Check/Money Order</span>
                        </td>
                      </tr>
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">COD</span>
                        </td>
                      </tr>                       
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Pay With WorldPay</span>
                        </td>
                      </tr>    
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Pay With PayPal</span>
                        </td>
                      </tr>    
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>                      
                      <#if billingAccountList?has_content>                                                         
                        <#list billingAccountList as billingAccount>                          
                          <tr>
                            <td align="left" valign="top" width="1%" nowrap>                             
                              <input type="radio" name="checkOutPaymentId" value="EXT_BILLACT|${billingAccount.billingAccountId?if_exists}" <#if ((cart.getGrandTotal()?double + billingAccount.accountBalance?double) > billingAccount.accountLimit?double)>disabled<#elseif selectedBillingAccountId?default("") == billingAccount.billingAccountId>checked</#if>>
                            </td>
                            <td align="left" valign="top" width="99%" nowrap>
                              <div class="tabletext">
                               Bill Account #<b>${billingAccount.billingAccountId}</b>&nbsp;(${(billingAccount.accountLimit?double - billingAccount.accountBalance)?string.currency})<br>
                               ${billingAccount.description?if_exists} 
                              </div> 
                            </td>
                          </tr>
                          <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        </#list>
                      </#if>                      
                      <#list context.paymentMethodList as paymentMethod>
                        <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                          <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                          <tr>                 
                            <td width="1%" nowrap>
                              <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                            </td>
                            <td width="50%" nowrap>
                              <span class="tabletext">CC:&nbsp;${Static["org.ofbiz.commonapp.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>
                              <a href="javascript:submitForm(document.checkoutInfoForm, 'EC', '${paymentMethod.paymentMethodId}');" class="buttontext">[Update]</a>
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                          <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                          <tr>
                            <td width="1%" nowrap>             
                              <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                            </td>
                            <td width="50%" nowrap>
                              <span class="tabletext">EFT:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</span>
                              <a href="javascript:submitForm(document.checkoutInfoForm, 'EE', '${paymentMethod.paymentMethodId}');" class="buttontext">[Update]</a>
                            </td>
                          </tr>
                          <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        </#if>
                      </#list>
                    </table>                    
                    <#if !paymentMethodList?has_content>                 
                      <div class='tabletext'><b>There are no payment methods on file.</b></div>
                    </#if>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>

<table width="100%">
  <tr valign="top">
    <td align="left">
      &nbsp;<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="buttontextbig">[Back to Shopping Cart]</a>
    </td>
    <td align="right">
      <a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" class="buttontextbig">[Continue to Final Order Review]</a>
    </td>
  </tr>
</table>
