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
 *@version    $Revision: 1.3 $
 *@since      3.0
-->

<script language="javascript">
<!-- //
function shipBillAddr() {
    if (document.billsetupform.useShipAddr.checked) {
        window.location.replace("setBilling?createNew=Y&finalizeMode=payment&paymentMethodType=${paymentMethodType?if_exists}&useShipAddr=Y");
    } else {
        window.location.replace("setBilling?createNew=Y&finalizeMode=payment&paymentMethodType=${paymentMethodType?if_exists}");
    }
}
</script>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align='left'>
            <div class='boxhead'>&nbsp;Payment Information</div>
          </td>
          <td nowrap align="right">
            <div class="tabletext">
              ${pages.get("/order/anonymoustrail.ftl")}
            </div>
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
            <#if (paymentMethodType?exists && !requestParameters.resetType?has_content) || finalizeMode?default("") == "payment">
              <#-- after initial screen; show detailed screens for selected type -->
              <#if paymentMethodType == "CC">
                <#if creditCard?has_content && postalAddress?has_content>
                  <form method="post" action="<@ofbizUrl>/changeCreditCardAndBillingAddress</@ofbizUrl>" name="billsetupform">
                    <input type="hidden" name="paymentMethodId" value="${creditCard.paymentMethodId?if_exists}">
                    <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}">
                <#elseif requestParameters.useShipAddr?exists>
                  <form method="post" action="<@ofbizUrl>/enterCreditCard</@ofbizUrl>" name="billsetupform">
                <#else>
                  <form method="post" action="<@ofbizUrl>/enterCreditCardAndBillingAddress</@ofbizUrl>" name="billsetupform">
                </#if>
              </#if>
              <#if paymentMethodType == "EFT">
                <#if eftAccount?has_content && postalAddress?has_content>
                  <form method="post" action="<@ofbizUrl>/changeEftAccountAndBillingAddress</@ofbizUrl>" name="billsetupform">
                    <input type="hidden" name="paymentMethodId" value="${eftAccount.paymentMethodId?if_exists}">
                    <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}">
                <#elseif requestParameters.useShipAddr?exists>
                  <form method="post" action="<@ofbizUrl>/enterEftAccount</@ofbizUrl>" name="billsetupform">
                <#else>
                  <form method="post" action="<@ofbizUrl>/enterEftAccountAndBillingAddress</@ofbizUrl>" name="billsetupform">
                </#if>
              </#if>

              <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
              <input type="hidden" name="partyId" value="${sessionAttributes.orderPartyId}">
              <input type="hidden" name="paymentMethodType" value="${paymentMethodType}">
              <input type="hidden" name="finalizeMode" value="payment">
              <input type="hidden" name="createNew" value="Y">
              <#if requestParameters.useShipAddr?exists>
                <input type="hidden" name="contactMechId" value="${postalFields.contactMechId}">
              </#if>

              <table width="100%" border="0" cellpadding="1" cellspacing="0">
                <#if cart.getShippingContactMechId()?exists>
                  <tr>
                    <td width="26%" align="right"= valign="top">
                      <input type="checkbox" name="useShipAddr" value="Y" onClick="javascript:shipBillAddr();" <#if requestParameters.useShipAddr?exists>checked</#if>>
                    </td>
                    <td colspan="2" align="left" valign="center">
                      <div class="tabletext">Billing address is the same as the shipping address</div>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="3"><hr class="sepbar"></td>
                  </tr>
                </#if>

                <#if (paymentMethodType == "CC" || paymentMethodType == "EFT")>
                  ${pages.get("/order/genericaddress.ftl")}
                </#if>

                <#-- gift card fields -->
                <#if paymentMethodType == "GC">
                  <#if !giftCard?has_content>
                    <#assign giftCard = requestParameters>
                  </#if>
                  <tr>
                    <td colspan="3"><hr class="sepbar"></td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Physical Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="20" maxlength="60" name="physicalNumber" value="${giftCard.physicalNumber?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Physical PIN</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="10" maxlength="60" name="physicalPin" value="${giftCard.physicalPin?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Virtual Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="20" maxlength="60" name="virtualNumber" value="${giftCard.virtualNumber?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Virtual PIN</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="10" maxlength="60" name="virtualPin" value="${giftCard.virtualPin?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Expiration Date</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <#assign expMonth = "">
                      <#assign expYear = "">
                      <#if giftCard?exists && giftCard.expireDate?exists>
                        <#assign expDate = giftCard.expireDate>
                        <#if (expDate?exists && expDate.indexOf("/") > 0)>
                          <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
                          <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
                        </#if>
                      </#if>
                      <select name="expMonth" class='selectBox'>
                        <#if giftCard?has_content && expMonth?has_content>
                          <#assign ccExprMonth = expMonth>
                        <#else>
                          <#assign ccExprMonth = requestParameters.expMonth?if_exists>
                        </#if>
                        <#if ccExprMonth?has_content>
                          <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
                        </#if>
                        ${pages.get("/includes/ccmonths.ftl")}
                      </select>
                      <select name="expYear" class='selectBox'>
                        <#if giftCard?has_content && expYear?has_content>
                          <#assign ccExprYear = expYear>
                        <#else>
                          <#assign ccExprYear = requestParameters.expYear?if_exists>
                        </#if>
                        <#if ccExprYear?has_content>
                          <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
                        </#if>
                        ${pages.get("/includes/ccyears.ftl")}
                      </select>
                    </td>
                  </tr>
                </#if>

                <#-- credit card fields -->
                <#if paymentMethodType == "CC">
                  <#if !creditCard?has_content>
                    <#assign creditCard = requestParameters>
                  </#if>
                  <tr>
                    <td colspan="3"><hr class="sepbar"></td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Name on Card</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="nameOnCard" value="${creditCard.nameOnCard?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Company Name on Card</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="companyNameOnCard" value="${creditCard.companyNameOnCard?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Card Type</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="cardType" class="selectBox">
                        <#if creditCard.cardType?exists>
                          <option>${creditCard.cardType}</option>
                          <option value="${creditCard.cardType}">---</option>
                        </#if>
                        ${pages.get("/includes/cctypes.ftl")}
                      </select>
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Card Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="20" maxlength="30" name="cardNumber" value="${creditCard.cardNumber?if_exists}">
                    *</td>
                  </tr>
                  <#--<tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Card Security Code</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" size="5" maxlength="10" name="cardSecurityCode" value="">
                    </td>
                  </tr>-->
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Expiration Date</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <#assign expMonth = "">
                      <#assign expYear = "">
                      <#if creditCard?exists && creditCard.expireDate?exists>
                        <#assign expDate = creditCard.expireDate>
                        <#if (expDate?exists && expDate.indexOf("/") > 0)>
                          <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
                          <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
                        </#if>
                      </#if>
                      <select name="expMonth" class='selectBox'>
                        <#if creditCard?has_content && expMonth?has_content>
                          <#assign ccExprMonth = expMonth>
                        <#else>
                          <#assign ccExprMonth = requestParameters.expMonth?if_exists>
                        </#if>
                        <#if ccExprMonth?has_content>
                          <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
                        </#if>
                        ${pages.get("/includes/ccmonths.ftl")}
                      </select>
                      <select name="expYear" class='selectBox'>
                        <#if creditCard?has_content && expYear?has_content>
                          <#assign ccExprYear = expYear>
                        <#else>
                          <#assign ccExprYear = requestParameters.expYear?if_exists>
                        </#if>
                        <#if ccExprYear?has_content>
                          <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
                        </#if>
                        ${pages.get("/includes/ccyears.ftl")}
                      </select>
                    *</td>
                  </tr>
                </#if>

                <#-- eft fields -->
                <#if paymentMethodType =="EFT">
                  <#if !eftAccount?has_content>
                    <#assign eftAccount = requestParameters>
                  </#if>
                  <tr>
                    <td colspan="3"><hr class="sepbar"></td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Name on Account</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="nameOnAccount" value="${eftAccount.nameOnAccount?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Company Name on Account</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="companyNameOnAccount" value="${eftAccount.companyNameOnAccount?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Bank Name</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="bankName" value="${eftAccount.bankName?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Routing Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="10" maxlength="30" name="routingNumber" value="${eftAccount.routingNumber?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Account Type</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="accountType" class='selectBox'>
                        <option>${eftAccount.accountType?if_exists}</option>
                        <option></option>
                        <option>Checking</option>
                        <option>Savings</option>
                      </select>
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Account Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="20" maxlength="40" name="accountNumber" value="${eftAccount.accountNumber?if_exists}">
                    *</td>
                  </tr>
                </#if>
                <tr>
                  <td align="center" colspan="3">
                    <input type="submit" class="smallsubmit" value="Continue">
                  </td>
                </tr>
              </table>
            <#else>
              <#-- initial screen show a list of options -->
              <form method="post" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" name="billsetupform">
                <input type="hidden" name="finalizeMode" value="payoption">
                <input type="hidden" name="createNew" value="Y">
                <table width="100%" border="0" cellpadding="1" cellspacing="0">
                  <tr>
                    <td width='5%' nowrap><input type="radio" name="paymentMethodType" value="offline" <#if paymentMethodType?exists && paymentMethodType == "offline">checked</#if></td>
                    <td width='95%'nowrap><div class="tabletext">Offline Payment: Check/Money Order</div></td>
                  </tr>
                  <tr><td colspan="2"><hr class='sepbar'></td></tr>
                  <tr>
                    <td width='5%' nowrap><input type="radio" name="paymentMethodType" value="CC" <#if paymentMethodType?exists && paymentMethodType == "CC">checked</#if></td>
                    <td width='95%' nowrap><div class="tabletext">Credit Card: Visa/Mastercard/Amex/Discover</div></td>
                  </tr>
                  <tr><td colspan="2"><hr class='sepbar'></td></tr>
                  <tr>
                    <td width='5%' nowrap><input type="radio" name="paymentMethodType" value="EFT" <#if paymentMethodType?exists && paymentMethodType == "EFT">checked</#if></td>
                    <td width='95%' nowrap><div class="tabletext">EFT Account: AHC/Electronic Check</div></td>
                  </tr>
                  <tr>
                    <td align="center" colspan="2">
                      <input type="submit" class="smallsubmit" value="Continue">
                    </td>
                  </tr>
                </table>
              </form>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
