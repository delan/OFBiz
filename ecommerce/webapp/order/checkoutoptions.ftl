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

<#assign cart = content.shoppingCart>

<form method="post" name="checkoutInfoForm" action="<@ofbizUrl>/checkout</@ofbizUrl>" style='margin:0;'>
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
                      <#list content.carrierShipmentMethodList as carrierShipmentMethod>                    
                        <tr>
                          <td width='1%' valign="top" >
                            <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
                            <input type='radio' name='shipping_method' value='${shippingMethod}' <#if shippingMethod == context.chosenShippingMethod>checked</#if>>       
                          </td>
                          <td valign="top">        
                            <#assign shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType")>
                            <div class='tabletext'>
                              ${shipmentMethodType.description?if_exists}&nbsp;
                              <#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}</#if>
                            </div>                           
                          </td>
                        </tr>
                      </#list>
                      <#if carrierShipmentMethodList?exists || carrierShipmentMethodList?size == 0>                     
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
                          <input type='radio' name="may_split' value='false' <#if !cart.getMaySplit()>checked</#if>>
                        </td>
                        <td valign="top">
                          <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top">
                          <input type='radio' name='may_split' value='true' <#if cart.getMaySplit()>checked</#if>>
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
                          <div>
                            <span class="head2"><b>Is This a Gift?</b></span>
                            <input type='radio' name='is_gift' value='true' <#if cart.getIsGift()>checked</#if>><span class='tabletext'>Yes</span>
                            <input type='radio' name='is_gift' value='false' <#if !cart.getIsGift()>checked</#if><span class='tabletext'>No</span>
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
                              <#list content.emailList as email>
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
                          <a href="<@ofbizUrl>/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</@ofbizUrl>" class="buttontext">[Add New Address]</a>
                        </td>
                      </tr>
                       <#if content.shippingContactMechList?has_content>
                         <tr><td colspan="2"><hr class='sepbar'></td></tr>
                         <#list content.shippingContactMechList as shippingContactMech>
                           <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
                           <tr>
                             <td align="left" valign="top" width="1%" nowrap>
                               <input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}" <#if cart.getShippingContactMechId() == shippingAddress.contactMechId>checked</#if>>        
                             </td>
                             <td align="left" valign="top" width="99%" nowrap>
                               <div class="tabletext">
                                 <#if shippingAddress.toName?exists><b>To:</b>&nbsp;${shippingAddress.toName}<br></#if>
                                 <#if shippingAddress.attnName?exists><b>Attn:</b>&nbsp;${shippingAddress.attnName}<br></#if>
                                 <#if shippingAddress.address1?exists>${shippingAddress.address1}<br></#if>
                                 <#if shippingAddress.address2?exists>${shippingAddress.address2}<br></#if>
                                 <#if shippingAddress.city?exists>${shippingAddress.city}</#if>
                                 <#if shippingAddress.stateProvidenceGeoId?exists><br>${shippingAddress.stateProvidenceId}</#if>
                                 <#if shippingAddress.postalCode?exists><br>${shippingAddress.postalCode}</#if>
                                 <#if shippingAddress.countryGeoId?exists><br>${shippingAddress.countryGeoId}</#if>                                                            
                                 <a href="<@ofbizUrl>/editcontactmech?DONE_PAGE=checkoutoptions&contactMechId=${shippingAddress.contactMechId}</@ofbizUrl>" class="buttontext">[Update]</a>
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
                        <a href="<@ofbizUrl>/editcreditcard?DONE_PAGE=checkoutoptions</@ofbizUrl>" class="buttontext">[Credit Card]</a>
                        <a href="<@ofbizUrl>/editeftaccount?DONE_PAGE=checkoutoptions</@ofbizUrl>" class="buttontext">[EFT Account]</a>
                      </td></tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == content.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Offline:&nbsp;Check/Money Order</span>
                        </td>
                      </tr> 
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == content.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Pay With WorldPay</span>
                        </td>
                      </tr>    
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == content.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Pay With PayPal</span>
                        </td>
                      </tr>    
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <#list content.paymentMethodList as paymentMethod>
                        <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                          <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                          <tr>                 
                            <td width="1%" nowrap>
                              <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                            </td>
                            <td width="50%" nowrap>
                              <span class="tabletext">CC:&nbsp;${Static["org.ofbiz.commonapp.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>
                              <a href="<@ofbizUrl>/editcreditcard?DONE_PAGE=checkoutoptions&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>" class="buttontext">[Update]</a>
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
                              <a href="<@ofbizUrl>/editeftaccount?DONE_PAGE=checkoutoptions&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>" class="buttontext">[Update]</a>
                            </td>
                          </tr>
                          <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        </#if>
                      </#list>
                    </table>
                    
                    <#if !paymentMethodList?has_content>                 
                      <div class='tabletext'><b>There are no payment methods on file.</b></div>
                    </#if>
                    <#if billingAccountRoleList?has_content>
                      <div class="tabletext">To pay with store credit, enter your Purchase Order (PO) number here and select the billing account:</div>
                      <input type="text" class='inputBox' name="corresponding_po_id" size="20" value='${cart.getPoNumber()?if_exists}'>
                      <br>
                      <table width="90%" border="0" cellpadding="0" cellspacing="0">
                        <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        <#list billingAccountRoleList as billingAccountRole>
                          <#assign billingAccount = billingAccountRole.getRelatedOne("BillingAccount")>
                          <tr>
                            <td align="left" valign="top" width="1%" nowrap>
                              <input type="radio" name="billing_account_id" value="${billingAccount.billingAccountId}" <#if cart.getBillingAccountId() == billingAccount.billingAccountId>checked</#if>>
                            </td>
                            <td align="left" valign="top" width="99%" nowrap>
                              <div class="tabletext">
                               Billing Account #<b>${billingAccount.billingAccountId}</b><br>
                               ${billingAccount.description?if_exists}
                              </div> 
                            </td>
                          </tr>
                          <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        </#list>
                      </table>
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
      &nbsp;<a href="<@ofbizUrl>/view/showcart</@ofbizUrl>" class="buttontextbig">[Back to Shopping Cart]</a>
    </td>
    <td align="right">
      <a href="javascript:document.checkoutInfoForm.submit()" class="buttontextbig">[Continue to Final Order Review]</a>
    </td>
  </tr>
</table>
