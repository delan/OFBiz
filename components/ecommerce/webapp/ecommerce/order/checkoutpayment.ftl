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
 *@version    $Revision: 1.11 $
 *@since      3.0
-->

<script language="javascript" type="text/javascript">
<!--
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; checkout
        form.action="<@ofbizUrl>/checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>/updateCheckoutOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutshippingaddress&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "GC") {
        // edit gift card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editgiftcard?paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }
}

function toggleBillingAccount(box) {
    var amountName = "amount_" + box.value;
    box.checked = true;
    box.form.elements[amountName].disabled = false;

    for (var i = 0; i < box.form.elements[box.name].length; i++) {
        if (!box.form.elements[box.name][i].checked) {
            box.form.elements["amount_" + box.form.elements[box.name][i].value].disabled = true;
        }
    }
}

// -->
</script>

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#assign cart = context.shoppingCart?if_exists>

<form method="post" name="checkoutInfoForm" style='margin:0;'>
  <input type="hidden" name="checkoutpage" value="payment">
  <input type="hidden" name="BACK_PAGE" value="checkoutoptions">
  <table width="100%" border="0" cellpadding='0' cellspacing='0'>
    <tr valign="top" align="left">

      <td bgcolor="white" width="1">&nbsp;&nbsp;</td>
      <td height='100%'>
        <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside' style='height: 100%;'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign=middle align=left>
                    <div class="boxhead">3)&nbsp;${uiLabelMap.OrderHowShallYouPay}?</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <#-- Payment Method Selection -->
          <tr style='height: 100%;'>
            <td width='100%' valign=top height='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
                <tr>
                  <td valign=top>
                    <table width="100%" cellpadding="1" cellspacing="0" border="0">
                      <tr><td colspan="2">
                        <span class='tabletext'>${uiLabelMap.CommonAdd}:</span>
                        <a href="javascript:submitForm(document.checkoutInfoForm, 'NC', '');" class="buttontext">[${uiLabelMap.AccountingCreditCard}]</a>
                        <a href="javascript:submitForm(document.checkoutInfoForm, 'NE', '');" class="buttontext">[${uiLabelMap.AccountingEftAccount}]</a>
                      </td></tr>
                      <tr><td colspan="3"><hr class='sepbar'></td></tr>
                      <tr>
                        <td width="1" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="1" nowrap>
                          <span class="tabletext">${uiLabelMap.OrderMoneyOrder}</span>
                        </td>
                        <td width="1" nowrap>&nbsp;</td>
                      </tr>
                      <tr>
                        <td width="1" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="1" nowrap>
                          <span class="tabletext">${uiLabelMap.OrderCOD}</span>
                        </td>
                        <td width="1" nowrap>&nbsp;</td>
                      </tr>
                      <tr>
                        <td width="1" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="1" nowrap>
                          <span class="tabletext">${uiLabelMap.AccountingPayWithWorldPay}</span>
                        </td>
                        <td width="1" nowrap>&nbsp;</td>
                      </tr>
                      <tr>
                        <td width="1" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="1" nowrap>
                          <span class="tabletext">${uiLabelMap.AccountingPayWithPayPal}</span>
                        </td>
                        <td width="1" nowrap>&nbsp;</td>
                      </tr>
                      <tr><td colspan="3"><hr class='sepbar'></td></tr>
                      <#list context.paymentMethodList as paymentMethod>
                        <#if paymentMethod.paymentMethodTypeId == "GIFT_CARD">
                          <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")>

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

                          <tr>
                            <td width="1%" nowrap>
                              <input type="checkbox" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if cart.isPaymentSelected(paymentMethod.paymentMethodId)>checked</#if>>
                            </td>
                            <td width="1%" nowrap>
                              <span class="tabletext">${uiLabelMap.AccountingGift}:&nbsp;${giftCardNumber}</span>
                              <span class="tabletext" align="right">
                                <a href="javascript:submitForm(document.checkoutInfoForm, 'EG', '${paymentMethod.paymentMethodId}');" class="buttontext">[${uiLabelMap.CommonUpdate}]</a>
                              </span>
                            </td>
                            <td>
                              &nbsp;
                              <span class="tabletext">
                                <b>${uiLabelMap.OrderBillUpTo}:</b> <input type="text" size="5" class="inputBox" name="amount_${paymentMethod.paymentMethodId}" value="<#if (cart.getPaymentAmount(paymentMethod.paymentMethodId)?default(0) > 0)>${cart.getPaymentAmount(paymentMethod.paymentMethodId)?double?string("##0.00")}</#if>">
                              </span>
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                          <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                          <tr>
                            <td width="1%" nowrap>
                              <input type="checkbox" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if cart.isPaymentSelected(paymentMethod.paymentMethodId)>checked</#if>>
                            </td>
                            <td width="1%" nowrap>
                              <span class="tabletext">CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>
                              <span class="tabletext" align="right">
                                <a href="javascript:submitForm(document.checkoutInfoForm, 'EC', '${paymentMethod.paymentMethodId}');" class="buttontext">[${uiLabelMap.CommonUpdate}]</a>
                              </span>
                            </td>
                            <td>
                              &nbsp;
                              <span class="tabletext">
                                <b>${uiLabelMap.OrderBillUpTo}:</b> <input type="text" size="5" class="inputBox" name="amount_${paymentMethod.paymentMethodId}" value="<#if (cart.getPaymentAmount(paymentMethod.paymentMethodId)?default(0) > 0)>${cart.getPaymentAmount(paymentMethod.paymentMethodId)?double?string("##0.00")}</#if>">
                              </span>
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                          <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                          <tr>
                            <td width="1%" nowrap>
                              <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                            </td>
                            <td width="1%" nowrap>
                              <span class="tabletext">EFT:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</span>
                              <a href="javascript:submitForm(document.checkoutInfoForm, 'EE', '${paymentMethod.paymentMethodId}');" class="buttontext">[${uiLabelMap.CommonUpdate}]</a>
                            </td>
                            <td>&nbsp;</td>
                          </tr>
                          <tr><td colspan="3"><hr class='sepbar'></td></tr>
                        </#if>
                      </#list>

                      <#-- special billing account functionality to allow use w/ a payment method -->
                      <#if billingAccountList?has_content>
                        <tr><td colspan="3"><hr class='sepbar'></td></tr>
                        <tr>
                          <td width="1%" nowrap>
                            <input type="radio" name="checkOutPaymentId" value="EXT_BILLACT" <#if "EXT_BILLACT" == checkOutPaymentId>checked</#if>></hr>
                          </td>
                          <td width="50%" nowrap>
                            <span class="tabletext">${uiLabelMap.AccountingPayOnlyWithBillingAccount}</span>
                          </td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr><td colspan="3"><hr class='sepbar'></td></tr>
                        <#list billingAccountList as billingAccount>
                          <#assign availableAmount = billingAccount.accountLimit?double - billingAccount.accountBalance?double>
                          <tr>
                            <td align="left" valign="top" width="1%" nowrap>
                              <input type="radio" onClick="javascript:toggleBillingAccount(this);" name="billingAccountId" value="${billingAccount.billingAccountId}" <#if (billingAccount.billingAccountId == selectedBillingAccount?default(""))>checked</#if>>
                            </td>
                            <td align="left" valign="top" width="99%" nowrap>
                              <div class="tabletext">
                               ${billingAccount.description?default("Bill Account")} #<b>${billingAccount.billingAccountId}</b>&nbsp;(<@ofbizCurrency amount=availableAmount isoCode=billingAccount.accountCurrencyUomId?default(cart.getCurrency())/>)<br>
                               <b>${uiLabelMap.OrderBillUpTo}:</b> <input type="text" size="5" class="inputBox" name="amount_${billingAccount.billingAccountId}" value="${availableAmount?double?string("##0.00")}" <#if !(billingAccount.billingAccountId == selectedBillingAccount?default(""))>disabled</#if>>
                              </div>
                            </td>
                            <td>&nbsp;</td>
                          </tr>
                        </#list>
                        <tr>
                          <td align="left" valign="top" width="1%" nowrap>
                            <input type="radio" onClick="javascript:toggleBillingAccount(this);" name="billingAccountId" value="_NA" <#if (selectedBillingAccount?default("") == "N")>checked</#if>>
                            <input type="hidden" name="_NA_amount" value="0.00">
                          </td>
                          <td align="left" valign="top" width="99%" nowrap>
                            <div class="tabletext">${uiLabelMap.AccountingNoBillingAccount}</div>
                           </td>
                           <td>&nbsp;</td>
                        </tr>
                      </#if>
                      <#-- end of special billing account functionality -->

                      <tr><td colspan="3"><hr class='sepbar'></td></tr>
                      <tr>
                        <td width="1%" nowrap>
                          <input type="checkbox" name="addGiftCard" value="Y">
                          <input type="hidden" name="singleUseGiftCard" value="Y">
                        </td>
                        <td colspan="2"nowrap>
                          <span class="tabletext">${uiLabelMap.AccountingUseGiftCardNotOnFile}</span>
                        </td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td width="1%" nowrap>
                          <div class="tabletext">${uiLabelMap.AccountingNumber}</div>
                        </td>
                        <td width="50%" nowrap>
                          <input type="text" size="15" class="inputBox" name="giftCardNumber" value="${(requestParameters.giftCardNumber)?if_exists}" onFocus="document.checkoutInfoForm.addGiftCard.checked=true;">
                        </td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td width="1%" nowrap>
                          <div class="tabletext">${uiLabelMap.AccountingPIN}</div>
                        </td>
                        <td width="50%" nowrap>
                          <input type="text" size="10" class="inputBox" name="giftCardPin" value="${(requestParameters.giftCardPin)?if_exists}" onFocus="document.checkoutInfoForm.addGiftCard.checked=true;">
                        </td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td width="1%" nowrap>
                          <div class="tabletext">${uiLabelMap.AccountingAmount}</div>
                        </td>
                        <td width="50%" nowrap>
                          <input type="text" size="6" class="inputBox" name="giftCardAmount" value="${(requestParameters.giftCardAmount)?if_exists}" onFocus="document.checkoutInfoForm.addGiftCard.checked=true;">
                        </td>
                      </tr>

                      <tr><td colspan="3"><hr class='sepbar'></td></tr>
                      <tr>
                        <td colspan="3">
                          <div class='tabletext' valign='middle'>
                            <a href="<@ofbizUrl>/setBilling?paymentMethodType=CC&singleUsePayment=Y</@ofbizUrl>" class="buttontext">[${uiLabelMap.AccountingSingleUseCreditCard}]</a>&nbsp;
                            <a href="<@ofbizUrl>/setBilling?paymentMethodType=GC&singleUsePayment=Y</@ofbizUrl>" class="buttontext">[${uiLabelMap.AccountingSingleUseGiftCard}]</a>&nbsp;
                            <a href="<@ofbizUrl>/setBilling?paymentMethodType=EFT&singleUsePayment=Y</@ofbizUrl>" class="buttontext">[${uiLabelMap.AccountingSingleUseEFTAccount}]</a>&nbsp;
                          </div>
                        </td>
                      </tr>
                    </table>
                    <#if !paymentMethodList?has_content>
                      <div class='tabletext'><b>${uiLabelMap.AccountingNoPaymentMethodsOnFile}.</b></div>
                    </#if>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <#-- End Payment Method Selection -->

        </table>
      </td>
    </tr>
  </table>
</form>

<table width="100%">
  <tr valign="top">
    <td align="left">
      &nbsp;<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="buttontextbig">[${uiLabelMap.OrderBacktoShoppingCart}]</a>
    </td>
    <td align="right">
      <a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" class="buttontextbig">[${uiLabelMap.OrderContinueToFinalOrderReview}]</a>
    </td>
  </tr>
</table>
